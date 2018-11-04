"""
This code is the SeeFood AI wrapper, exposing API calls over
the network so that we can call the SeeFood AI on the remote
EC2 instance from the mobile app.
"""

from flask import Flask, request, jsonify

app = Flask(__name__)

class SeeFoodAI:
    
    # This is the only call that is actually exposed over web
    @app.route("/api/ai-decision", methods=['POST'])
    def get_ai_decision():
        pass
        
    def _extract_food_confidence():
        pass
        
    def _extract_not_food_confidence():
        pass
        
    def _extract_sender():
        pass
