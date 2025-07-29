import { useEffect, useState } from "react";
import { OfferResponse } from "./types/Offer/OfferResponse";
import { useAuth } from "./AuthContext";
import { acceptOffer, deleteOfferById, fetchOfferByUser, rejectOffer } from "./api/OfferAPI";
import { Button } from "react-bootstrap";

const OfferList: React.FC = () => {

    const [sentOffers, setSentOffers] = useState<OfferResponse[]>([]);
    const [receivedOffers, setReceivedOffers] = useState<OfferResponse[]>([]);
    const [acceptedOffers, setAcceptedOffers] = useState<OfferResponse[]>([]);
    const [rejectedOffers, setRejectedOffers] = useState<OfferResponse[]>([]);
    const [errors, setErrors] = useState<string[]>([]);
    const { token, appUserId } = useAuth();

    useEffect(() => {
        fetchOffers();
    }, [appUserId])

    const fetchOffers = async () => {
        
        if (token && appUserId) {
            try {
                const data = await fetchOfferByUser(appUserId, token)

                const sent: OfferResponse[] = []
                const received: OfferResponse[] = []
                const rejected: OfferResponse[] = []
                const accepted: OfferResponse[] = []
                for (const offer of data) {
                    if (offer.userId === appUserId) {
                        sent.push(offer)
                    } else {
                        if(offer.offerStatus === "ACCEPTED") {
                            accepted.push(offer)
                        } else if (offer.offerStatus === "REJECTED") {
                            rejected.push(offer)
                        } else {
                            received.push(offer)
                        }
                    }
                }

                setSentOffers(sent);
                setReceivedOffers(received);
                setAcceptedOffers(accepted);
                setRejectedOffers(rejected);
            } catch (e) {
                console.log(e);
            }
        }
    }

    const deleteOffer = async (offerId: number) => {

        if (window.confirm(`Delete Offer?`)) {
            if (token) {
                try {
                    await deleteOfferById(offerId, token)
                    const newSentOffer = sentOffers.filter(offer => offer.offerId !== offerId)
                    setSentOffers(newSentOffer)
                } catch (e) {
                    console.log(e);
                }
            }
        }

    }

    const accept = async (offerId: number) => {
        if (window.confirm("Accept Offer?")) {
            if (token) {
                try {
                    const data = await acceptOffer(offerId, token)
                    const newOffers = receivedOffers.filter(offer => offer.offerId !== offerId)
                    setAcceptedOffers((prev) => [...prev, data])
                    setReceivedOffers(newOffers)
                } catch (e) {
                    console.log(e);
                }
            }
        }
    }

    useEffect(() => {
        console.log(rejectedOffers)
    }, [])


    const reject = async (offerId: number) => {
        if (window.confirm("Reject Offer?")) {
            if (token) {
                try {
                    const data = await rejectOffer(offerId, token);

                    const newOffers = receivedOffers.filter(offer => offer.offerId !== offerId)
                    setRejectedOffers((prev) => [...prev, data])
                    
                    setReceivedOffers(newOffers)
                    
                } catch (e) {
                    console.log(e);
                }
            }
        }
    }

    return (
        <>
            <div>
                <h2>Your Offers</h2>
                <ul>
                    {sentOffers.map((offer) => (
                        <div key={offer.offerId}>
                            {token && offer.userId === appUserId && offer.offerStatus === "PENDING" && (
                            <><li >{offer.offerStatus}</li>
                            
                                <Button
                                    className="mr-4 mb-2 mt-4"
                                    variant="danger"
                                    onClick={() => deleteOffer(offer.offerId)}
                                >
                                    Delete Offer
                                </Button></>
                            )}
                        </div>
                    ))}
                </ul>
            </div>
            <div>
                <h2>Your Offers Accepted</h2>
                <ul>
                    {sentOffers.map((offer) => (
                        <div key={offer.offerId}>
                            <div key={offer.offerId}>
                            {token && offer.userId === appUserId && offer.offerStatus === "ACCEPTED" && (
                            <><li >{offer.offerStatus}</li>
                            
                            </>
                            )}
                        </div>
                        </div>
                    ))}
                </ul>
            </div>
            <div>
                <h2>Your Offers Rejected/Expired</h2>
                <ul>
                    {sentOffers.map((offer) => (
                        <div key={offer.offerId}>
                            <div key={offer.offerId}>
                            {token && offer.userId === appUserId && (offer.offerStatus === "REJECTED" || offer.offerStatus === "EXPIRED")  && (
                            <><li >{offer.offerStatus}</li>
                            
                                </>  
                            )}
                        </div>
                        </div>
                    ))}
                </ul>
            </div>

            

            

<div>
                <h2>Incoming Offers</h2>

                <ul>
                    {receivedOffers.map((offer) => (

                        <div key={offer.offerId}>
                            <div>
                                {token && offer.offerStatus === "PENDING" && (
                                    <>
                                        <li>{offer.offerAmount}</li>
                                        <li>{offer.message}</li>
                                        <li>{offer.offerStatus}</li>
                                        <li>{offer.sentAt}</li>
                                        <li>{offer.productId}</li><div>
                                            <Button
                                                className="mr-4 mb-2 mt-4"
                                                variant="success"
                                                onClick={() => accept(offer.offerId)}
                                            >
                                                Accept
                                            </Button>
                                            <Button
                                                className="mr-4 mb-2 mt-4"
                                                variant="danger"
                                                onClick={() => reject(offer.offerId)}
                                            >
                                                Reject
                                            </Button>
                                            <p>---------------------</p>
                                        </div></>

                                )}
                            </div>

                        </div>


                    ))}
                </ul>
            </div>
            <div>
                <h2>Incoming Offers Accepted</h2>

                <ul>
                    {acceptedOffers.map((offer) => (

                        <div key={offer.offerId}>
                            <div>
                                {token && offer.offerStatus === "ACCEPTED" && (
                                    <>
                                        <li>{offer.offerAmount}</li>
                                        <li>{offer.message}</li>
                                        <li>{offer.offerStatus}</li>
                                        <li>{offer.sentAt}</li>
                                        <li>{offer.productId}</li><div>
                                            
                                            <p>---------------------</p>
                                        </div></>

                                )}
                            </div>

                        </div>


                    ))}
                </ul>
            </div>
            <div>
                <h2>Incoming Offers Rejected/Expired</h2>

                <ul>
                    {rejectedOffers.map((offer) => (

                        <div key={offer.offerId}>
                            <div>
                                {token && offer.offerStatus === "REJECTED" && (
                                    <>
                                        <li>{offer.offerAmount}</li>
                                        <li>{offer.message}</li>
                                        <li>{offer.offerStatus}</li>
                                        <li>{offer.sentAt}</li>
                                        <li>{offer.productId}</li><div>
                                            
                                            <p>---------------------</p>
                                        </div></>

                                )}
                            </div>

                        </div>


                    ))}
                </ul>
            </div>
        </>
    )
}

export default OfferList;