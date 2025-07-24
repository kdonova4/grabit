import { useEffect, useState } from "react";
import { OfferResponse } from "./types/Offer/OfferResponse";
import { useAuth } from "./AuthContext";
import { deleteOfferById, fetchOfferByUser } from "./api/OfferAPI";
import { Button } from "react-bootstrap";

const OfferList: React.FC = () => {

    const [sentOffers, setSentOffers] = useState<OfferResponse[]>([]);
    const [receivedOffers, setReceivedOffers] = useState<OfferResponse[]>([]);
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
                for (const offer of data) {
                    if (offer.userId === appUserId) {
                        sent.push(offer)
                    } else {
                        received.push(offer)
                    }
                }

                setSentOffers(sent);
                setReceivedOffers(received);
            } catch (e) {
                console.log(e);
            }
        }
    }

    const deleteOffer = async (offerId: number) => {
        
        if(window.confirm(`Delete Offer?`)) {
            if(token) {
            try{
                await deleteOfferById(offerId, token)
                const newSentOffer = sentOffers.filter(offer => offer.offerId !== offerId)
                setSentOffers(newSentOffer)
            } catch(e) {
                console.log(e);
            }
        }
        }
        
    }

    return (
        <>
            <div>
                <h2>Offers Sent</h2>
                <ul>
                    {sentOffers.map((offer) => (
                        <div key={offer.offerId}>
                            <li >{offer.offerStatus}</li>
                            {token && offer.userId === appUserId && (
                                <Button
                                    className="mr-4 mb-2 mt-4"
                                    variant="danger"
                                    onClick={() => deleteOffer(offer.offerId)}
                                >
                                    Delete Offer
                                </Button>
                            )}
                        </div>
                    ))}
                </ul>
            </div>

            <div>
                <h2>Offers Recieved</h2>

                <ul>
                    {receivedOffers.map((offer) => (

                        <li key={offer.offerId}><div>{offer.message}</div></li>


                    ))}
                </ul>
            </div>
        </>
    )
}

export default OfferList;