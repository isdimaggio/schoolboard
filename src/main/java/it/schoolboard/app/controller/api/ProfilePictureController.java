package it.schoolboard.app.controller.api;

import it.schoolboard.app.entity.ProfilePicture;
import it.schoolboard.app.service.ProfilePictureService;
import it.schoolboard.app.utility.S3Client;
import it.schoolboard.app.utility.SSOUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
public class ProfilePictureController {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ProfilePictureService profilePictureService;

    // elaborates the image and creates / updates entry
    // in the table
    @PostMapping("/api/profile-pictures/")
    public ResponseEntity<ResponseMessage> saveProfilePicture(
            @RequestParam("profile-picture") MultipartFile multipart
    ) {
        // check image size
        if (multipart.getSize() > 2097152) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(
                    false,
                    "The image provided was too big."
            ));
        }

        String kcUUID;
        try {
            // get session keycloak id
            SSOUser user = new SSOUser(SecurityContextHolder.getContext());
            kcUUID = user.getAccessTokenContent().getSubject();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage(
                    false,
                    "Forbidden"
            ));
        }

        // generate new UUID for profile picture
        UUID fileUUID = UUID.randomUUID();
        Path tempFile;

        try {
            // convert image to standard format
            BufferedImage inputImage = ImageIO.read(multipart.getInputStream());
            BufferedImage outputImage = new BufferedImage(
                    64, 64, // profile pictures should be 64x64
                    inputImage.getType()
            );
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, 64, 64, null);
            g2d.dispose();

            // tempFile
            tempFile = Files.createTempFile(fileUUID.toString(), ".png");
            ImageIO.write(outputImage, "png", tempFile.toFile());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(
                    false,
                    "The image provided was not readable by the server."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(
                    false,
                    "Internal server error"
            ));
        }

        String s3FileName = "propics/" + fileUUID + ".png";

        boolean ul = s3Client.uploadFile(
                tempFile.toString(),
                true,
                s3FileName
        );

        if (!ul) {
            // TODO: log this

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(
                    false,
                    "The image could not be stored in the S3 backend."
            ));
        }

        if (!tempFile.toFile().delete()) {
            // TODO: log this

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(
                    false,
                    "The temporary elaboration file could not be deleted."
            ));
        }

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setS3Location(s3FileName);
        profilePicture.setKeycloakUserId(kcUUID);

        if (profilePictureService.exists(kcUUID)) {

            ProfilePicture oldProfilePicture = profilePictureService.fetchProfilePicture(kcUUID);
            // s3 delete
            if (!s3Client.deleteFile(true, oldProfilePicture.getS3Location())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(
                        false, "Deletion in S3 storage for the old profile picture failed.")
                );
            }

            profilePictureService.updateProfilePicture(
                    profilePicture, kcUUID
            );

        } else {
            profilePictureService.saveProfilePicture(profilePicture);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(true, "OK"));
    }

    // read specific user's profile picture
    @GetMapping("/api/profile-pictures/{id}")
    public ProfilePicture fetchProfilePictureByID(
            @PathVariable("id") String keylcloadUserID) {
        ProfilePicture profilePicture = profilePictureService.fetchProfilePicture(keylcloadUserID);
        profilePicture.setS3Location(
                s3Client.getPublicBucketURI() + profilePicture.getS3Location()
        );
        return profilePicture;
    }

    // read own profile picture information
    @GetMapping("/api/profile-pictures/")
    public ProfilePicture fetchProfilePictureBySession() {
        // get session info here
        SSOUser user = new SSOUser(SecurityContextHolder.getContext());
        ProfilePicture profilePicture = profilePictureService.fetchProfilePicture(
                user.getAccessTokenContent().getSubject()
        );
        profilePicture.setS3Location(
                s3Client.getPublicBucketURI() + profilePicture.getS3Location()
        );
        return profilePicture;
    }

    // Delete operation
    @DeleteMapping("/api/profile-pictures/")
    public ResponseEntity<ResponseMessage> deleteProfilePictureBySession() {
        // get session info here
        String kcUUID;
        try {
            // get session keycloak id
            SSOUser user = new SSOUser(SecurityContextHolder.getContext());
            kcUUID = user.getAccessTokenContent().getSubject();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage(
                    false,
                    "Forbidden"
            ));
        }

        if (profilePictureService.exists(kcUUID)) {
            ProfilePicture oldProfilePicture = profilePictureService.fetchProfilePicture(kcUUID);
            profilePictureService.deleteProfilePictureById(kcUUID);

            // s3 delete
            if (s3Client.deleteFile(true, oldProfilePicture.getS3Location())) {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(true, "OK"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(
                        false, "Deletion in S3 storage for the old profile picture failed.")
                );
            }

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(
                    false, "There is no profile picture to delete")
            );
        }
    }

}
