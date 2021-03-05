from tensorflow import keras
from tensorflow.keras import layers

model = keras.Sequential()
model.add(layers.Dense(units=100))
model.add(layers.Dense(units=10, activation='relu'))