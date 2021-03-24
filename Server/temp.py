from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras import losses
from tensorflow.keras import optimizers
from sklearn import preprocessing
from sklearn import metrics
import pandas as pd
import numpy as np
from sklearn import model_selection

df = pd.read_csv("D:\\FYP\\Code\\Server\\Uploads\\uploadstayyabnasem@gmail.com\\IRIS_preprocessed.csv")

df['species'] = preprocessing.LabelEncoder().fit_transform(df['species'])

print(df.head())

# X = df.loc[:, df.columns != 'species']
# Y = df['species']
# Y = preprocessing.LabelEncoder().fit_transform(Y)
# Y = keras.utils.to_categorical(Y)

# X_train,X_test,y_train,y_test = model_selection.train_test_split(X,Y,test_size=0.2)

# print(X_train.shape[0])
# # print(y_train.shape)
# # print(X_test.shape)
# # print(y_test.shape)

# model = keras.Sequential()

# model.add(layers.Dense(10,input_shape=(4,),activation='tanh'))
# model.add(layers.Dense(8,activation='tanh'))
# model.add(layers.Dense(6,activation='tanh'))
# model.add(layers.Dense(3,activation='softmax'))
# # model.add(layers.Dense(1,activation='softmax'))

# opt = optimizers.Adam(learning_rate=0.04)
# loss = losses.BinaryCrossentropy()
# model.compile(loss = loss, optimizer = opt, metrics = ['accuracy'])

# model.fit(X_train, y_train, batch_size=64, epochs=50, verbose=1, validation_data=(X_test, y_test))

# y_pred = model.predict(X_test)
# y_pred = np.argmax(y_pred, axis=1)
# y_test = np.argmax(y_test, axis=1) 
# print(y_pred)
# print(y_test)
# # print(y_)
# print(metrics.confusion_matrix(y_test, y_pred))