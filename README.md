# MethodSequenceRecommender

This is the python secript for Running Bi directional LSTM based ENcoder Decoder with ATtention and Beam Search.

Requirements:

python 3.5 or more

pip3


Installation Step:

1. Clone the repository using following command: 

"git clone -b pythonimpl0.1 --single-branch https://github.com/khaledkucse/DeepAPIMethodCallReccomendation.git"

2. Download a sample model file from the following link. This model files will help you to test and testone


https://drive.google.com/open?id=1BzmtzGw5UonELQREjdv0K-sw_ar9K9wP

3. First install the dependency. Run following command:

"pip3 install -r requirements.txt"

4. Next open the config.py and configure the variables and parameter.
mind it you need to change the name of the model file according to the 
Try not to change top_k. Currently we are supporting 1,2,5,and 10 only

5. Open __main__.py or you can run by command line using follwoing command:

"python3 __main__.py"

it will want input showing following line:

Please enter one of the mode: 
 train : To train the model 
 test: To test the model 
 train-test: To train and then test 
 testone: To see result for a single instance 
 
 
 write "train" if you want train, "test" if you want to test over number of test cases and "testone" if want to test a single instance
 
 
 6. Next it will ask whether you want to load existing vocabulary. For test and testone you should load give "y" other wise its not that important.
 
 Then you will see the progress or output in the console.
