/*
    SchoolBoard: the free and open source all-in-one platform for school
    Copyright (C) 2020-2022 Lo Mele Vittorio
    Copyright (C) 2022 Flowopia Network

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    INFO:           info@schoolboard.it
    COMMERCIAL:     commerciale@schoolboard.it
    DEVELOPEMENT:   sviluppo@schoolboard.it
 */

package it.schoolboard.app.service;

import it.schoolboard.app.entity.ProfilePicture;
import it.schoolboard.app.repository.ProfilePictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProfilePictureServiceImpl
        implements ProfilePictureService {
    @Autowired
    private ProfilePictureRepository profilePictureRepository;

    // Save operation
    @Override
    public ProfilePicture saveProfilePicture(ProfilePicture profilePicture) {
        return profilePictureRepository.save(profilePicture);
    }

    @Override
    public ProfilePicture fetchProfilePicture(String keycloakUserId) {
        return profilePictureRepository.findById(keycloakUserId)
                .get();
    }

    public boolean exists(String keycloakUserId) {
        return profilePictureRepository.findById(keycloakUserId)
                .isPresent();
    }

    // Update operation
    @Override
    public ProfilePicture
    updateProfilePicture(ProfilePicture profilePicture,
                         String keycloakUserId) {
        ProfilePicture ppDB
                = profilePictureRepository.findById(keycloakUserId)
                .get();

        if (Objects.nonNull(profilePicture.getS3Location())
                && !"".equalsIgnoreCase(
                profilePicture.getS3Location())) {
            ppDB.setS3Location(
                    profilePicture.getS3Location());
        }

        return profilePictureRepository.save(ppDB);
    }

    // Delete operation
    @Override
    public void deleteProfilePictureById(String keycloakUserId) {
        profilePictureRepository.deleteById(keycloakUserId);
    }
}
