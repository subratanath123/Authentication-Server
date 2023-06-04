package net.auzy.service.user;

import net.auzy.entity.user.Address;
import net.auzy.entity.idp.IdpUser;
import net.auzy.entity.user.User;
import net.auzy.service.common.WebClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.auzy.entity.idp.IdpProvider;
import net.auzy.entity.user.AddressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

import static java.time.LocalDate.of;
import static net.auzy.entity.user.Role.PROFILE;

@Component
public class GoogleUserMapperService {

    @Autowired
    private WebClientService webClientService;

    public User parseUserProfile(IdpUser idpUser) throws JsonProcessingException {
        String userProfileJson = getUserProfileJson(idpUser);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode userProfile = objectMapper.readTree(userProfileJson);


        User user = new User();
        JsonNode names = userProfile.get("names");

        if (names != null && !names.isNull()) {
            for (JsonNode nameNode : names) {
                String displayNameLastFirst = nameNode.get("displayNameLastFirst").asText();

                user.setFirstName(displayNameLastFirst.substring(displayNameLastFirst.indexOf(",")) + 1);
                user.setLastName(displayNameLastFirst.substring(0, displayNameLastFirst.indexOf(",")));
                user.setDisplayName(displayNameLastFirst);

                if (user.getFirstName() != null && user.getLastName() != null) {
                    break;
                }
            }
        }

        user.setEmail(idpUser.getEmail());
        user.setVerifiedUser(true);
        user.setRoleList(List.of(PROFILE));

        JsonNode genders = userProfile.get("genders");

        if (genders != null && !genders.isNull()) {
            for (JsonNode genderNode : genders) {
                String gender = genderNode.get("formattedValue").asText();
                user.setGender(gender);

                if (user.getGender() != null) {
                    break;
                }
            }
        }

        JsonNode birthdays = userProfile.get("birthdays");

        if (birthdays != null && !birthdays.isNull()) {
            for (JsonNode birthdayNode : birthdays) {
                int year = birthdayNode.get("date").get("year").asInt();
                int month = birthdayNode.get("date").get("month").asInt();
                int day = birthdayNode.get("date").get("day").asInt();

                user.setBirthDate(of(year, month, day));

                if (user.getBirthDate() != null) {
                    break;
                }
            }
        }


        JsonNode coverPhotos = userProfile.get("coverPhotos");

        if (coverPhotos != null && !coverPhotos.isNull()) {
            for (JsonNode coverPhotoNode : coverPhotos) {
                String coverPhotoUrl = coverPhotoNode.get("url").asText();
                user.setCoverPhotoUrl(coverPhotoUrl);
                //have to check default = true

                if (user.getCoverPhotoUrl() != null) {
                    break;
                }
            }
        }

        user.getIdpProviderList().add(IdpProvider.GOOGLE);
        user.setProfilePictureUrl(idpUser.getProfilePictureUrl());


        Address address = new Address();
        address.setAddressType(AddressType.HOME);
        //TODO:: Have to get and add lat lng
        address.setCity("Dhaka");
        address.setCountry("Bangladesh");

        user.getAddressList().add(address);

        return user;
    }

    private String getUserProfileJson(IdpUser idpUser) {
        Consumer<HttpHeaders> headersConsumer = headers
                -> headers.setBearerAuth(idpUser.getTokenInfo().getAccessToken());

        return webClientService.makeGetRestCall(
                "https://people.googleapis.com/v1/people/me?personFields=locations,phoneNumbers,coverPhotos,names,emailAddresses,phoneNumbers,birthdays,urls,addresses,genders",
                headersConsumer,
                String.class
        ).block();
    }


}
