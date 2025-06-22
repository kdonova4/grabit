package com.kdonova4.grabit.domain.mapper;

import com.kdonova4.grabit.model.AppUser;
import com.kdonova4.grabit.model.Product;
import com.kdonova4.grabit.model.Watchlist;
import com.kdonova4.grabit.model.WatchlistDTO;

public class WatchlistMapper {

    public static WatchlistDTO toDTO(Watchlist watchlist) {
        return new WatchlistDTO(
                watchlist.getWatchId(),
                watchlist.getProduct().getProductId(),
                watchlist.getUser().getAppUserId()
        );
    }

    public static Watchlist toWatchlist(WatchlistDTO watchlistDTO, Product product, AppUser user) {
        return new Watchlist(
                0,
                product,
                user
        );
    }
}
