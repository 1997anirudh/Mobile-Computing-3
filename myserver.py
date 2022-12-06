import flask
import os
import numpy as np 
import pandas as pd 
from PIL import Image
import cv2
from keras.models import load_model

model = load_model("/Users/pawanramaswamy/Documents/TestProject/model.h5")
server = flask.Flask(__name__)
@server.route('/', methods=['GET', 'POST'])


def handle_request():
    capturedImage = flask.request.files['image']
    img = Image.open(capturedImage)
    img = np.asarray(img)
    arr_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    arr_img = np.expand_dims(arr_img, 2)
    size=(28, 28)
    arr_img=cv2.resize(arr_img, size)
    img_data = np.array(arr_img)/255
    img_data = np.array(img_data.flatten())
    if np.average(img_data) > 0.5:
        img_data = 1 - img_data
    predictedResult=model.predict(img_data.reshape(1, 784))[0]
    a = 0
    value = 0
    for i in range(len(predictedResult)):
        if predictedResult[i] > value:
            a = i
            value = predictedResult[i]
    print(f"Prediction is: %s" % a)
    predictedResult = a
    savePath="./"+predictedResult+"/"
    savePathExists = os.path.exists(savePath)
    if not savePathExists:
        os.makedirs(savePath)
    capturedImage.save(savePath+capturedImage.filename)
    print(predictedResult)
    return predictedResult

server.run(host="0.0.0.0", port=5001, debug=True)