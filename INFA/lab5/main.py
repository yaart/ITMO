import matplotlib.pyplot as plt
import pandas as pd

df = pd.read_csv("data11.csv")

fig, ax = plt.subplots(figsize=(12, 8))

df.groupby('<DATE>').boxplot(column=["<OPEN>", "<HIGH>", "<LOW>", "<CLOSE>"], ax=ax)

# Показать график
plt.show()
