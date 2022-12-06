import sys
import cv2
import numpy as np 
import pickle
import pandas as pd 
from PIL import Image

from keras.datasets import mnist
from keras.models import Sequential
from keras.layers import Dense, Activation, Dropout
from tensorflow.keras.utils import to_categorical




(x_train, y_train),(x_test, y_test) = mnist.load_data()

num_labels = len(np.unique(y_train))
y_train = to_categorical(y_train)
y_test = to_categorical(y_test)

data_dim = x_train.shape[1] * x_train.shape[1]

x_train = np.reshape(x_train, [-1, data_dim])
x_train = x_train.astype('float32') / 255
x_test = np.reshape(x_test, [-1, data_dim])
x_test = x_test.astype('float32') / 255

# Source - https://www.tensorflow.org/datasets/keras_example
model = Sequential()
model.add(Dense(256, input_dim=data_dim))
model.add(Activation('relu'))
model.add(Dropout(0.45))
model.add(Dense(256))
model.add(Activation('relu'))
model.add(Dropout(0.45))
model.add(Dense(num_labels))
model.add(Activation('softmax'))

model.summary()

model.compile(loss='categorical_crossentropy', 
              optimizer='adam',
              metrics=['accuracy'])

model.fit(x_train, y_train, epochs=20, batch_size=128)

loss, acc = model.evaluate(x_test, y_test, batch_size=128)
print("\nTest accuracy: %.1f%%" % (100.0 * acc))
model.save("/Users/pawanramaswamy/Documents/TestProject/model.h5")