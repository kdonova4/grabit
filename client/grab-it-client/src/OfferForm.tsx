import { useState } from "react";
import { OfferRequest } from "./types/Offer/OfferRequest";
import { useAuth } from "./AuthContext";
import { useParams } from "react-router-dom";
import { createOffer } from "./api/OfferAPI";
import { Button, Form, Modal } from "react-bootstrap";
    
interface OfferProps {
    onClose: () => void;
    showModal: boolean;
}

const OFFER_DEFAULT: OfferRequest = {
        offerAmount: 0,
        message: "",
        userId: 0,
        productId: 0,
    }

const OfferForm: React.FC<OfferProps> = ({ onClose, showModal }) => {


    const [offer, setOffer] = useState<OfferRequest>(OFFER_DEFAULT);
    const [errors, setErrors] = useState<string[]>([])
    const { token, appUserId } = useAuth();
    const { id } = useParams();
    

    const handleSubmit = (e: React.FormEvent<HTMLButtonElement>) => {
        e.preventDefault();
        addOffer();
        
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        setOffer((prev) => ({
            ...prev,
            [name]: name === "offerAmount" ? Number(value) : value
        }))
    }

    const addOffer = async () => {
            if (!token || !appUserId) {
                setErrors(["User Not Authenticated"]);
                return;
            }
    
            const createRequest: OfferRequest = {
                offerAmount: offer.offerAmount,
                message: offer.message,
                userId: appUserId,
                productId: Number(id)
            }
    
            try {
                const data = await createOffer(createRequest, token);
                console.log(data)
                onClose();
            } catch (e) {
                if (Array.isArray(e)) {
                    setErrors(e)
                }
            }
        }

    return (
        <>
<Modal style={{color: `black`}} show={showModal}>
                <Modal.Header>
                    <Modal.Title>Make Offer</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                            {errors.length > 0 && (
                                <div className="alert alert-danger">
                                    <p>The following Errors were found:</p>
                                    <ul>
                                        {errors.map(error => (
                                            <li key={error}>{error}</li>
                                        ))}
                                    </ul>
                                </div>
                            )}
                            <Form.Label>Offer Amount</Form.Label>
                            <Form.Control
                                type="number"
                                name="offerAmount"
                                step="0.10"
                                min="0.00"

                                placeholder="0.00"
                                value={offer.offerAmount}
                                onChange={handleChange}
                                autoFocus
                                required
                            />
                            <Form.Control
                                type="text"
                                name="message"
                                value={offer.message}
                                onChange={handleChange}
                                autoFocus
                                required
                            />
                        </Form.Group>
                        
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onClose}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleSubmit}>
                        Send
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default OfferForm;