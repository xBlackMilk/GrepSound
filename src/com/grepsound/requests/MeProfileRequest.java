package com.grepsound.requests;

import com.grepsound.model.Profile;
import com.grepsound.services.SpiceUpService;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Http;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.json.JSONObject;


/**
 * Created by lisional on 2014-04-18.
 */
public class MeProfileRequest extends SpiceRequest<Profile> {

    Token token;

    public MeProfileRequest(Token tok) {
        super(Profile.class);
        token = tok;
    }

    @Override
    public Profile loadDataFromNetwork() throws Exception {
        ApiWrapper wrapper = new ApiWrapper(SpiceUpService.CLIENT_ID, SpiceUpService.CLIENT_SECRET, null, token) ;

        HttpResponse resp = wrapper.get(Request.to("/me"));
        JSONObject result = Http.getJSON(resp);
        Profile profile = new Profile(result);

        return profile;
    }
}