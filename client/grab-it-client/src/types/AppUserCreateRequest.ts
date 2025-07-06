export interface AppUserCreateRequest {
    userId: number;
    username: string;
    email: string;
    roles: string[];
}