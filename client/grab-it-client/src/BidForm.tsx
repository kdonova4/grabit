import { useState } from "react";
import { BidCreateRequest } from "./types/Bid/BidCreateRequest";
import { useParams } from "react-router-dom";
import { useAuth } from "./AuthContext";
import { createBid } from "./api/BidAPI";
import { Button, Form, Modal } from "react-bootstrap";

const BID_DEFAULT: BidCreateRequest = {
    userId: 0,
    productId: 0,
    bidAmount: 0.00
}

interface BidProps {
    onClose: () => void;
    showModal: boolean;
}

const BidForm: React.FC<BidProps> = ({ onClose, showModal }) => {

    const [bid, setBid] = useState<BidCreateRequest>(BID_DEFAULT);
    const { id } = useParams();
    const { token, appUserId } = useAuth();
    const [errors, setErrors] = useState<string[]>([])
    



    const handleSubmit = (e: React.FormEvent<HTMLButtonElement>) => {
        e.preventDefault();
        addBid();
        
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;

        setBid((prev) => ({
            ...prev,
            [name]: name === "bidAmount" ? Number(value) : value
        }))
    }

    const addBid = async () => {
        if (!token || !appUserId) {
            setErrors(["User Not Authenticated"]);
            return;
        }

        const createRequest: BidCreateRequest = {
            userId: appUserId,
            productId: Number(id),
            bidAmount: bid.bidAmount
        }

        try {
            const data = await createBid(createRequest, token);
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
                    <Modal.Title>Place Bid</Modal.Title>
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
                            <Form.Label>Bid Amount</Form.Label>
                            <Form.Control
                                type="number"
                                name="bidAmount"
                                step="0.10"
                                min="0.00"

                                placeholder="0.00"
                                value={bid.bidAmount}
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
                        Place
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}

export default BidForm;