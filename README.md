# DeepAPIMethodCall

This is the full implementation of Deep API Method Call Reccomendation. The program takes java source files(.java) as input
and then reccomend api method call with the arguments as a sequence.

There are three branches in this repository.

contextcollection0.1: The java program that collect contexts from java source files. 

pythonimpl0.1: The python script that implements Bi-directional LSTM based Encoder Decoder with Attention and Beam Search. 
It takes context returned from context collection java program. Tensorflow and Keras is used at the backend.

javaimpl0.1: Its a java program that implements LSTM based Encoder Decoder with Beam Search. The implemetation is same as the python script with same input and output.

