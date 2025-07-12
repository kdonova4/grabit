import { createContext, ReactNode, useContext, useEffect, useState } from "react"

interface AuthContextType {
    token: string | null;
    roles: string[] | null;
    login: (newToken: string) => void;
    logout: () => void;
    appUserId: number | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}



export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {

    const [token, setToken] = useState<string | null>(
        localStorage.getItem("token")
    );
    const [roles, setRoles] = useState<string[] | null>(null);
    const [appUserId, setAppUserId] = useState<number | null>(null);

    useEffect(() => {
        let interval: NodeJS.Timeout;

        if(token) {
            try{
                const decodedToken = JSON.parse(atob(token.split(".")[1]));

                setRoles(decodedToken.authorities ?? null);
                setAppUserId(decodedToken.appUserId ?? null);

                interval = setInterval(() => {
                    if(decodedToken.exp * 1000 < Date.now()) {
                        logout();
                        clearInterval(interval);
                        alert("Your session has expired. Please log in again.")
                    }
                }, 1000)

                localStorage.setItem("token", token);
            } catch(e) {
                console.error("Invalid Token format", e);
                logout();
            }
        } else {
            setRoles(null);
            setAppUserId(null);
            localStorage.removeItem("token");
        }

        return () => clearInterval(interval);
    }, [token]);

    const login = (newToken: string) => {
        setToken(newToken);
    }

    const logout = () => {
        setToken(null);
        setRoles(null);
        setAppUserId(null);
    };

    return (
        <>
            <AuthContext.Provider value={{ token, roles, login, logout, appUserId }}>
                {children}
            </AuthContext.Provider>   
        </>
    )
}

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if(!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }

    return context;
}