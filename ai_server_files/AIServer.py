#! /usr/bin/python

"""
This code is the SeeFood AI wrapper, exposing API calls over
the network so that we can call the SeeFood AI on the remote
EC2 instance from the mobile app.
"""

from flask import Flask, request, jsonify
import numpy as np
import tensorflow as tf
from PIL import Image
from datetime import datetime
from os.path import isfile

# The below block is from Dr. Derek Doran's find_food.py example
###### Initialization code - we only need to run this once and keep in memory.
sess = tf.Session()
saver = tf.train.import_meta_graph('saved_model/model_epoch5.ckpt.meta')
saver.restore(sess, tf.train.latest_checkpoint('saved_model/'))
graph = tf.get_default_graph()
x_input = graph.get_tensor_by_name('Input_xn/Placeholder:0')
keep_prob = graph.get_tensor_by_name('Placeholder:0')
class_scores = graph.get_tensor_by_name("fc8/fc8:0")
######

# Initialize this Flask app
app = Flask(__name__)

# This is a sort of pseudo-class... Poss re-add later
# class SeeFoodAI:
    
# This is the only call that is actually exposed over web
# Also note this code is heavily inspired by Dr. Derek Doran's
#   find_food.py example
@app.route("/api/ai-decision", methods=['POST'])
def get_ai_decision():
    # Generate a file name
    file_name = "/home/ubuntu/seefood/images/" + datetime.now().strftime("%Y%m%d%H%M%S")
    image_number = 1
    while isfile(file_name):
        file_name += str(image_number)
        image_number += 1
        
    # Save off the file in our images database
    with open(file_name, 'wb+') as out:
        out.write(request.files.get("image").read())
        
    # Create a tensor from the image
    # TODO: See if we still want to resize in utils, or if this works
    image = Image.open(file_name).convert('RGB')
    image = image.resize((227, 227), Image.BILINEAR)
    img_tensor = [np.asarray(image, dtype=np.float32)]
    
    #Run the image in the model.
    scores = sess.run(class_scores, {x_input: img_tensor, keep_prob: 1.})
    
    # Return the result   
    return file_name + "," + str(scores[0][0]) + "," + str(scores[0][1])
    
def _extract_food_confidence():
    pass
    
def _extract_not_food_confidence():
    pass
    
def _extract_sender():
    pass
    
# Open up the API to the internets
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
