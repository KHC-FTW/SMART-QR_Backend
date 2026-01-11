package com.khchan744.smart_qr_backend.repo;

import com.khchan744.smart_qr_backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepo extends JpaRepository<AppUser,String> {
    AppUser findByUsername(String username);
}
