package com.easemybooking.places.controller;

import com.easemybooking.places.model.Place;
import com.easemybooking.places.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.security.disabled=false"  // enable security in test
})
@AutoConfigureMockMvc
class PlaceControllerSecurityTest {

    @Autowired MockMvc mvc;
    @Autowired PlaceRepository repo;

    UUID ownerId = UUID.randomUUID();
    UUID otherOwnerId = UUID.randomUUID();
    UUID placeIdOwned;

    @BeforeEach
    void setup() {
        repo.deleteAll();
        Place p = Place.builder()
                .placeId(UUID.randomUUID())
                .name("Test Place")
                .city("City")
                .description("Desc")
                .capacity(100)
                .pricePerSlot(new BigDecimal("10"))
                .ownerId(ownerId)
                .build();
        repo.save(p);
        placeIdOwned = p.getPlaceId();
    }

    @Test
    void user_can_list_but_cannot_create() throws Exception {
        mvc.perform(get("/places"))
                .andExpect(status().isOk());

        mvc.perform(post("/places")
                        .with(jwt().jwt(j -> j.claim("sub", UUID.randomUUID().toString())
                                .claim("roles", new String[]{"USER"})))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"name":"X","city":"Y","description":"Z","capacity":10,"pricePerSlot":50}
                         """))
                .andExpect(status().isForbidden()); // needs OWNER/ADMIN
    }

    @Test
    void owner_can_update_his_own_place() throws Exception {
        mvc.perform(put("/places/{id}", placeIdOwned)
                        .with(jwt().jwt(j -> j.claim("sub", ownerId.toString())
                                .claim("roles", new String[]{"OWNER"})))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"name":"New","city":"City","description":"New","capacity":120,"pricePerSlot":15}
                         """))
                .andExpect(status().isOk());
    }

    @Test
    void owner_cannot_update_foreign_place() throws Exception {
        mvc.perform(put("/places/{id}", placeIdOwned)
                        .with(jwt().jwt(j -> j.claim("sub", otherOwnerId.toString())
                                .claim("roles", new String[]{"OWNER"})))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                         {"name":"Hack","city":"City","description":"Hack","capacity":1,"pricePerSlot":1}
                         """))
                .andExpect(status().isForbidden()); // blocked by @IsOwnerOrAdmin -> @placeAuth.canModify
    }

    @Test
    void admin_can_delete_any_place() throws Exception {
        mvc.perform(delete("/places/{id}", placeIdOwned)
                        .with(jwt().jwt(j -> j.claim("sub", UUID.randomUUID().toString())
                                .claim("roles", new String[]{"ADMIN"}))))
                .andExpect(status().isNoContent());
    }
}
