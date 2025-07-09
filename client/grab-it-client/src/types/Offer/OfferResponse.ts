export type OfferStatus = 'PENDING' | 'REJECTED' | 'ACCEPTED' | 'EXPIRED';

export interface OfferResponse {
    offerId: number;
    offerAmount: number;
    sentAt: string;
    message: string;
    userId: number;
    productId: number;
    expireDate: Date;
    offerStatus: OfferStatus;
}