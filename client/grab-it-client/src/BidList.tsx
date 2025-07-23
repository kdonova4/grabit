import { useEffect, useState } from "react";
import { BidResponse } from "./types/Bid/BidResponse";
import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { useParams } from "react-router-dom";
import { deleteBidById, fetchByProduct } from "./api/BidAPI";
import { useAuth } from "./AuthContext";
import { Button } from "react-bootstrap";

const BidList: React.FC = () => {

    const [bids, setBids] = useState<BidResponse[]>([])
    const [connected, setConnected] = useState<boolean>(false);
    const { id } = useParams();
    const { token, appUserId } = useAuth();



    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');

        const stompClient = new Client({
            webSocketFactory: () => socket as any,
            debug: (str) => {
                console.log("[STOMP]", str);
            },
            reconnectDelay: 5000,
            onConnect: () => {
                setConnected(true);
                const subscription = stompClient.subscribe(
                    `/topic/bids/${id}`,
                    (message: IMessage) => {
                        console.log("MESSAGE RECIEVED:", message.body)
                        const bidData: BidResponse[] = JSON.parse(message.body);
                        setBids(bidData)
                    }
                );
            },
            onStompError: (frame) => {
                console.error("Broker reported error: " + frame.headers["message"]);
                console.error("Details: " + frame.body);
            }
        })

        stompClient.activate();
        console.log("BIDS:" + bids)
        fetchBids();

        return () => {
            stompClient.deactivate();
            setConnected(false);
            setBids([]);
        }
    }, [id])


    const fetchBids = async () => {
        try {
            const data = await fetchByProduct(Number(id))
            setBids(data);
            console.log(data)
        } catch (e) {
            console.log(e)
        }
    }

    const deleteBid = async (bidId: number) => {
        if(token) {
            try {
            await deleteBidById(bidId, token);
        } catch (e) {
            console.log(e);
        }
        }
        
    }

    return (
        <>
            <div>
                <h2>Bids for product {id}</h2>
                {!connected && <p>Loading...</p>}
                <ul>
                    {bids.map((bid) => (
                        <div>
                            <li key={bid.bidId}>Bid #{bid.bidId}: ${bid.bidAmount}</li>
                            {token && bid.userId === appUserId && (
                                <Button
                                    className="mr-4 mb-2 mt-4"
                                    variant="danger"
                                    onClick={() => deleteBid(bid.bidId)}
                                >
                                    Delete
                                </Button>
                            )}
                        </div>

                    ))}
                </ul>
            </div>
        </>
    )
}

export default BidList;