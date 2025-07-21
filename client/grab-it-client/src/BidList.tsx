import { useEffect, useState } from "react";
import { BidResponse } from "./types/Bid/BidResponse";
import SockJS from "sockjs-client";
import { Client, IMessage } from "@stomp/stompjs";
import { useParams } from "react-router-dom";

const BidList: React.FC = () => {

    const [bids, setBids] = useState<BidResponse[]>([])
    const [connected, setConnected] = useState<boolean>(false);
    const { id } = useParams();
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

        return () => {
            stompClient.deactivate();
            setConnected(false);
            setBids([]);
        }
    }, [id])

    return (
        <>
            <div>
                <h2>Bids for product {id}</h2>
                {!connected && <p>Connecting to WebSocket...</p>}
                <ul>
                    {bids.map((bid) => (
                        <li key={bid.bidId}>Bid #{bid.bidId}: ${bid.bidAmount}</li>
                    ))}
                </ul>
            </div>
        </>
    )
}

export default BidList;