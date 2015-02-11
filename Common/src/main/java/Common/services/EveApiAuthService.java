package Common.services;

import com.beimin.eveapi.account.characters.CharactersParser;
import com.beimin.eveapi.account.characters.CharactersResponse;
import com.beimin.eveapi.account.characters.EveCharacter;
import com.beimin.eveapi.core.ApiAuthorization;
import com.beimin.eveapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class EveApiAuthService {
    @Value("${eve.api.keyId}")
    private int keyId;

    @Value("${eve.api.verificationCode}")
    private String verificationCode;
    private ApiAuthorization apiAuthorization;

    public ApiAuthorization getApiAuthorization() throws ApiException {
        if (apiAuthorization == null) {
            CharactersParser parser = CharactersParser.getInstance();

            ApiAuthorization auth = new ApiAuthorization(keyId, verificationCode);
            CharactersResponse response = parser.getResponse(auth);
            Collection<EveCharacter> eveCharacters = response.getAll();

            apiAuthorization = new ApiAuthorization(keyId, eveCharacters.iterator().next().getCharacterID(), verificationCode);
        }
        return apiAuthorization;
    }
}
