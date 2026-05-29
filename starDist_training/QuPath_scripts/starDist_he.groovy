import qupath.ext.stardist.StarDist2D
import qupath.lib.scripting.QP
 
// define path to model 
def modelPath = "PATH/TO/he_heavy_augment.pb"


def stardist = StarDist2D
    .builder(modelPath)
     .preprocess(      
        ImageOps.Filters.median(2),           
        //ImageOps.Filters.gaussianBlur(2)
    )
    .normalizePercentiles(1, 99) // Percentile normalization
    .threshold(0.5)              // Probability (detection) threshold
    .pixelSize(0.3)              // Resolution for detection
    //.cellExpansion(0.5) 
    .measureShape()              // Add shape measurements
    .measureIntensity()          // Add nucleus measurements
    .build()
	 

def pathObjects = QP.getSelectedObjects()
//def pathObjects = QP.getAnnotationObjects()
 
// Run detection for the selected objects
def imageData = QP.getCurrentImageData()
if (pathObjects.isEmpty()) {
    QP.getLogger().error("No parent objects are selected!")
    return
}
stardist.detectObjects(imageData, pathObjects)
stardist.close() // This can help clean up & regain memory
println('Done!')