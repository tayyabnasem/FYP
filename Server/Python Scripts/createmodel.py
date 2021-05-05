import sys, json

def create_dense_layer(units, activation_function):
    layer_str = f"model.add(layers.Dense(units={units}"
    if activation_function != 'default':
        layer_str += f", activation='{activation_function}'))"
    else:
        layer_str += f"))"
    return layer_str

def create_dropout_layer(dropout_rate):
    layer_str = f"model.add(layers.Dropout(rate={dropout_rate}))"
    return layer_str

lines = sys.stdin.readlines()
filepath = lines[0].replace('\n', '')
data = json.loads(lines[1])

model_layers = data['layers']
hyperparameters = data['hyperparameters']


lines = ["from tensorflow import keras",
         "from tensorflow.keras import layers", 
         "from tensorflow.keras import losses",
         "from tensorflow.keras import optimizers",
         "import pandas as pd", 
         "import numpy as np",
         "from sklearn.model_selection import train_test_split",
         f"df = pd.read_csv(r'{filepath}')","",
         f"X = df.loc[:, df.columns != '{hyperparameters['output_coulmn']}']",
         f"Y = df['{hyperparameters['output_coulmn']}']","",
         f"X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - hyperparameters['validation_split']) / 100}, random_state=42)",
         "model = keras.Sequential()", "",  
         ]

for layer in model_layers:
    if layer['layerName'] == 'Dense':
        lines.append(create_dense_layer(layer['units'], layer['activationFunction'].lower()))
    elif layer['layerName'] == 'Dropout':
        lines.append(create_dropout_layer(layer['dropoutRate']))

lines.append("")
lines.append(f"opt = optimizers.{hyperparameters['optimizer']}(learning_rate={hyperparameters['learningRate']})") 
lines.append(f"loss = losses.{hyperparameters['lossFunction'].replace(' ', '')}()")
lines.append(f"model.compile(loss = loss, optimizer = opt, metrics = ['accuracy'])")
# lines.append(f"try:")
lines.append(f"model.fit(X_train, y_train, batch_size={hyperparameters['batchSize']}, epochs={hyperparameters['epoch']})")
# lines.append(f"except:")
# lines.append("\tprint()")

temp_path = filepath.split('\\')[:-1]
temp_path.append('model.py')
file_name = '/'.join(temp_path)
print(file_name)

with open(file_name, 'w') as file:
    file.write("\n".join(lines))