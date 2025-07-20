import { createContext, ReactNode, useContext, useEffect, useState } from "react";
import { WatchlistDTO } from "./types/Watchlist/WatchlistDTO"
import { useAuth } from "./AuthContext";
import { createWatchItem, deleteWatchItemById, findWatchItemByUserAndProduct, findWatchItemsByUser } from "./api/WatchlistAPI";

interface WatchContextType {
    watchlist: WatchlistDTO[];
    addToWatchlist: (productId: number) => void;
    removeFromWatchlist: (productId: number) => void;
    fetchWatchlist: () => void;
    itemInWatchlist: (productId: number) => Promise<boolean>;
}

const WatchContext = createContext<WatchContextType | undefined>(undefined);

interface WatchProviderProps {
    children: ReactNode;
}

export const WatchProvider: React.FC<WatchProviderProps> = ({ children }) => {
    const [watchlist, setWatchlist] = useState<WatchlistDTO[]>([]);
    const { token, appUserId } = useAuth();
    const [errors, setErrors] = useState<string[]>([]);

    useEffect(() => {
        if(token && appUserId) {
            fetchWatchlist();
        }
    }, [token, appUserId])

    const fetchWatchlist = async () => {
        if (token && appUserId) {
            try {
                const data = await findWatchItemsByUser(appUserId, token);
                setWatchlist(data);
                console.log(data)
            } catch (e) {
                console.log(e);
                setErrors((prev) => [...prev, (e as Error).message])
            }
        } else {
            console.log("not working")
        }
    }

    const addToWatchlist = async (productId: number) => {

        if(token && appUserId) {

            const watchItem: WatchlistDTO = {
                watchId: 0,
                productId: productId,
                userId: appUserId
            }

            try{
                const data = await createWatchItem(watchItem, token)
                setWatchlist((prev) => [...prev, data]);
            } catch(e) {
                console.log(e);
            }
        }

    }

    const itemInWatchlist = async (productId: number): Promise<boolean> => {
        if(token) {

            try{
                const item = watchlist.find(item => item.productId === productId)
                if(item) {
                    return true;
                } else {
                    return false;
                }
            } catch(e) {
                console.log(e);
                return false;
            }
        }

        return false;
    }

    const removeFromWatchlist = async (productId: number) => {

        if(token && appUserId) {

            const item = watchlist.find(item => item.productId === productId);

            if(!item) {
                setErrors(["Watch Item Not Found"])
                return;
            }

            try {
                await deleteWatchItemById(item.watchId, token)
                const newWatchlist = watchlist.filter(watchItem => watchItem.watchId !== item.watchId)
                setWatchlist(newWatchlist);
            } catch(e) {
                console.log(e);
            }

        }

    }

    return (
        <>
            <WatchContext.Provider value={{ watchlist, addToWatchlist, removeFromWatchlist, itemInWatchlist, fetchWatchlist}}>
                { children }
            </WatchContext.Provider>
        </>
    )
}

export const useWatch = (): WatchContextType => {
    const context = useContext(WatchContext);
    if(!context) {
        throw new Error("useWatch must be within a WatchProvider")
    }

    return context;
}

