package com.khchan744.smart_qr_backend.service;

import com.khchan744.smart_qr_backend.model.AppUser;
import com.khchan744.smart_qr_backend.model.AppUserDetails;
import com.khchan744.smart_qr_backend.repo.AppUserRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepo appUserRepo;

    @Override
    public AppUserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepo.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("Failed to find user with the given username: " + username);
        }
        return new AppUserDetails(appUser);
    }
}
