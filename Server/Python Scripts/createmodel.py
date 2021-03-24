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

data = sys.stdin.readlines()
data = json.loads(data[0])

model_layers = data['layers']
hyperparameters = data['hyperparameters']


lines = ["from tensorflow import keras",
         "from tensorflow.keras import layers", 
         "from tensorflow.keras import losses",
         "from tensorflow.keras import optimizers", "",
         "model = keras.Sequential()", ""]

for layer in model_layers:
    if layer['layerName'] == 'Dense':
        lines.append(create_dense_layer(layer['Dense']['units'], layer['Dense']['activationFunction'].lower()))
    elif layer['layerName'] == 'Dropout':
        lines.append(create_dropout_layer(layer['Dropout']['dropoutRate']))

lines.append("")
lines.append(f"opt = optimizers.{hyperparameters['optimizer']}(learning_rate={hyperparameters['learningRate']})") 
lines.append(f"loss = losses.{hyperparameters['lossFunction'].replace(' ', '')}()")
lines.append(f"model.compile(loss = loss, optimizer = opt, metrics = ['accuracy'])")



with open('temp.py', 'w') as file:
    file.write("\n".join(lines))