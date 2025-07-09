import { AppUserAuthRequest } from "../types/AppUser/AppUserAuthRequest";
import { AppUserAuthResponse } from "../types/AppUser/AppUserAuthResponse";
import { AppUserCreateRequest } from "../types/AppUser/AppUserCreateRequest";
import { AppUserCreateResponse } from "../types/AppUser/AppUserCreateResponse";
import { AppUserVerify } from "../types/AppUser/AppUserVerify";

export async function register(user: AppUserCreateRequest): Promise<AppUserCreateResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/users/register/user`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user)
    });

    if(response.status == 400) {
        throw new Error("Bad Credentials")
    }

    if (!response.ok) {
    throw new Error("Failed to register user");
    }

    const data: AppUserCreateResponse = await response.json();
    return data;
}

export async function authenticate(credentials: AppUserAuthRequest): Promise<AppUserAuthResponse> {
    const response = await fetch(`http://localhost:8080/api/v1/users/authenticate`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials)
    });

    if(response.status === 403) {
        throw new Error("Invalid Username and/or Password")
    }

    if(!response.ok) {
        throw new Error("Failed to Login")
    }

    const data: AppUserAuthResponse = await response.json();
    return data;
}

export async function verify(credentials: AppUserVerify): Promise<void> {
    const response = await fetch(`http://localhost:8080/api/v1/users/register/verify`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials)
    });

    if(response.status === 400) {
        throw new Error("Invalid Verification Code")
    }

    if(!response.ok) {
        throw new Error("Failed to verify user")
    }
}


