package security;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dto.MemeDTO;
import entities.Meme;
import facades.UserFacade;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import java.util.ArrayList;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

@Path("login")
public class LoginEndpoint {

  public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
  private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
  public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String login(String jsonString) throws AuthenticationException {
    JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
    String username = json.get("username").getAsString();
    String password = json.get("password").getAsString();

    try {
      User user = USER_FACADE.getVerifiedUser(username, password);
      String token = createToken(user);
      List<MemeDTO> upvotedMemes = new ArrayList<>();
      List<MemeDTO> downvotedMemes = new ArrayList<>();
      
      for (Meme meme : user.getUpvotedMemes()) {
          upvotedMemes.add(new MemeDTO(meme));
      }
      for (Meme meme : user.getDownvotedMemes()) {
          downvotedMemes.add(new MemeDTO(meme));
      }
      
      JsonElement upvotes = new Gson().toJsonTree(upvotedMemes, new TypeToken<List<MemeDTO>>() {}.getType());
      JsonElement downvotes = new Gson().toJsonTree(downvotedMemes, new TypeToken<List<MemeDTO>>() {}.getType());
      JsonArray upvoteArray = upvotes.getAsJsonArray();
      JsonArray downvoteArray = downvotes.getAsJsonArray();
      
      JsonObject responseJson = new JsonObject();
      responseJson.addProperty("username", username);
      responseJson.addProperty("token", token);
      responseJson.addProperty("profilePicture", user.getProfilePicture());
      responseJson.add("upvotedMemes", upvoteArray);
      responseJson.add("downvotedMemes", downvoteArray);
      return new Gson().toJson(responseJson);

    } catch (JOSEException | AuthenticationException ex) {
      if (ex instanceof AuthenticationException) {
        throw (AuthenticationException) ex;
      }
      Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new AuthenticationException("Invalid username or password! Please try again");
  }

  private String createToken(User user) throws JOSEException {
    StringBuilder res = new StringBuilder();
    for (String string : user.getRolesAsStrings()) {
      res.append(string);
      res.append(",");
    }
    String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";
    String issuer = "semesterstartcode-dat3";

    JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
    Date date = new Date();
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(user.getUsername())
            .claim("username", user.getUsername())
            .claim("roles", rolesAsString)
            .claim("issuer", issuer)
            .issueTime(date)
            .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
            .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(signer);
    return signedJWT.serialize();

  }
}