import pandas as pd
import matplotlib.pyplot as plt

import seaborn as sns

df = pd.read_csv('D:\FYP\Code\Server\Python Scripts\IRIS.csv')

# sns.distplot(df['sepal_length'], hist=True)
sns.histplot(data=df, x="sepal_length", hue="species")

#sns.scatterplot(x=df.index, y=df['sepal_length'], hue=df['species'])
# plt.savefig('graph.png')
plt.show()



print(df.index)