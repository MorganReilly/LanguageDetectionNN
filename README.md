# Language Detection NN Project
## Morgan Reilly -- G00303598

### To Clone Repo
`$ git clone https://github.com/MorganReilly/LanguageDetectionNN.git`
`$ cd LangaugeDetectionNN`

### Running Application
* First ensure you have these two:
	* encog-jar-3.X.jar -> Project can't run without it (3.4 supplied in repo)
	* wili-2018-Small-11750 -> Data file used for training and testing
* Assuming you are in ./LanguageDetectionNN
* Run with:
`$ java -cp ./language-nn.jar:./encog-core-3.4.jar ie.gmit.sw.Runner`

### About Project
See README.pdf

#### References
* https://s3.amazonaws.com/heatonresearch-books/free/encog-3_3-quickstart.pdf
* http://www.jmlr.org/papers/volume16/heaton15a/heaton15a.pdf
* https://en.wikipedia.org/wiki/Backpropagation
* https://s3.amazonaws.com/heatonresearch-books/free/encog-3_3-devguide.pdf
* http://heatonresearch-site.s3-website-us-east-1.amazonaws.com/javadoc/encog-3.3/org/encog/ml/data/folded/FoldedDataSet.html
* https://s3.amazonaws.com/heatonresearch-books/free/Encog3Java-User.pdf
* http://heatonresearch-site.s3-website-us-east-1.amazonaws.com/javadoc/encog-3.3/org/encog/util/simple/EncogUtility.html#convertCSV2Binary(java.io.File,%20java.io.File,%20int,%20int,%20boolean)
* http://heatonresearch-site.s3-website-us-east-1.amazonaws.com/javadoc/encog-3.3/org/encog/neural/networks/training/propagation/resilient/ResilientPropagation.html
* http://heatonresearch-site.s3-website-us-east-1.amazonaws.com/javadoc/encog-3.3/org/encog/ml/data/MLDataSet.html
