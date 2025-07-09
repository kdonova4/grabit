import { CouponDTO } from "../types/Coupon/CouponDTO";

export async function fetchCouponByCode(couponCode: string): Promise<CouponDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/coupons/coupon/${couponCode}`);

    if(!response.ok) {
        throw new Error(`Coupon with code: ${couponCode} not found`);
    }

    const data: CouponDTO = await response.json();

    return data;
}

export async function fetchCouponById(couponId: number): Promise<CouponDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/coupons/${couponId}`);

    if(!response.ok) {
        throw new Error(`Coupon ID ${couponId} Not Found`)
    }

    const data: CouponDTO = await response.json();
    return data;
}

export async function createBid(coupon: CouponDTO): Promise<CouponDTO> {
    const response = await fetch(`http://localhost:8080/api/v1/coupons`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(coupon)
    });

    if(response.status !== 201) {
        throw new Error("Error Creating New Bid")
    }

    return await response.json();
}

export async function deleteBidById(couponId: number): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/coupons/${couponId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });

    if(response.status !== 204) {
        throw new Error(`Unexpected Status Code ${response.status}`)
    }
}