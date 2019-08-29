package de.adorsys.xs2a.adapter.rest.psd2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class UpdatePsuAuthenticationResponseTO {
    private AuthenticationObjectTO chosenScaMethod;

    private ChallengeDataTO challengeData;

    private List<AuthenticationObjectTO> scaMethods;

    @JsonProperty("_links")
    private Map<String, HrefTypeTO> links;

    private String scaStatus;

    private String psuMessage;

    public AuthenticationObjectTO getChosenScaMethod() {
        return chosenScaMethod;
    }

    public void setChosenScaMethod(AuthenticationObjectTO chosenScaMethod) {
        this.chosenScaMethod = chosenScaMethod;
    }

    public ChallengeDataTO getChallengeData() {
        return challengeData;
    }

    public void setChallengeData(ChallengeDataTO challengeData) {
        this.challengeData = challengeData;
    }

    public List<AuthenticationObjectTO> getScaMethods() {
        return scaMethods;
    }

    public void setScaMethods(List<AuthenticationObjectTO> scaMethods) {
        this.scaMethods = scaMethods;
    }

    public Map<String, HrefTypeTO> getLinks() {
        return links;
    }

    public void setLinks(Map<String, HrefTypeTO> links) {
        this.links = links;
    }

    public String getScaStatus() {
        return scaStatus;
    }

    public void setScaStatus(String scaStatus) {
        this.scaStatus = scaStatus;
    }

    public String getPsuMessage() {
        return psuMessage;
    }

    public void setPsuMessage(String psuMessage) {
        this.psuMessage = psuMessage;
    }
}
