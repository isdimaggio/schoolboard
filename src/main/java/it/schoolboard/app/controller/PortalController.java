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

package it.schoolboard.app.controller;

import it.schoolboard.app.entity.ProfilePicture;
import it.schoolboard.app.service.ProfilePictureService;
import it.schoolboard.app.utility.S3Client;
import it.schoolboard.app.utility.SSOUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// this controller handles all calls to the pages of the "portal"
@Controller
public class PortalController {

    @Autowired
    BuildProperties buildProperties;
    @Autowired
    ProfilePictureService profilePictureService;
    @Autowired
    S3Client s3Client;

    // homepage, contains the profile selector or the public content
    @GetMapping("/")
    public String index(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        model.addAttribute("ppUri", getProfilePictureURI(user));
        return "pages/portal/index";
    }

    @GetMapping("/info/privacy")
    public String privacyPolicy(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        model.addAttribute("ppUri", getProfilePictureURI(user));
        return "pages/portal/info/privacy";
    }

    @GetMapping("/info/legalNotes")
    public String legalNotes(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        model.addAttribute("ppUri", getProfilePictureURI(user));
        return "pages/portal/info/legalNotes";
    }

    @GetMapping("/info/credits")
    public String credits(Model model) {
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        model.addAttribute("user", user);
        model.addAttribute("ppUri", getProfilePictureURI(user));
        // get software version / BUILD
        model.addAttribute("buildProp", buildProperties);
        return "pages/portal/info/credits";
    }

    public String getProfilePictureURI(SSOUser user) {
        if (user.getAccessTokenContent() == null) return "/static/img/default-avatar.svg";
        if (profilePictureService.exists(user.getAccessTokenContent().getSubject())) {
            ProfilePicture profilePicture = profilePictureService.fetchProfilePicture(
                    user.getAccessTokenContent().getSubject()
            );
            return s3Client.getPublicBucketURI() + profilePicture.getS3Location();
        } else {
            return "/static/img/default-avatar.svg";
        }
    }
}
