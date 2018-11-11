package com.blastbeatsandcode.seefood.model;

import android.media.Image;

public class SeeFoodAI {
    private SeeFoodAI _aiInstance;

    SeeFoodAI ()
    {
        // TODO: Implement this ??
    }

    // Send image to AI server and get a response
    public SFImage getAIDecision(Image image)
    {
        // TODO: Implement this
        return new SFImage(image);
    }

    private float extractFoodConfidence()
    {
        // TODO: Implement this
        return 0.0f;
    }

    private float extractNotFoodConfidence()
    {
        // TODO: Implemeent this
        return 0.0f;
    }

    private String extractSender()
    {
        // TODO: Implement this
        return "";
    }
}
