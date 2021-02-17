import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers

model = keras.Sequential()
model.add(layers.Dense(24))

model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

for layer in model.layers:
    print(layer.name)

