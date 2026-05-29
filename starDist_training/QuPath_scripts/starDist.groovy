import qupath.ext.stardist.StarDist2D
import qupath.lib.scripting.QP
import qupath.opencv.ops.ImageOps

//define path to your starDist model
def modelPath = "PATH/TO/dsb2018_heavy_augment.pb"

var imageData = QP.getCurrentImageData()
var stains = imageData.getColorDeconvolutionStains()

def stardist = StarDist2D
    .builder(modelPath)
    .preprocess( 
        ImageOps.Channels.deconvolve(stains), 
        ImageOps.Channels.extract(0, 1),      
        ImageOps.Channels.sum(), 
        ImageOps.Filters.median(2)                
    )
    .normalizePercentiles(1, 99) 
    .threshold(0.5)              
    .includeProbability(true)
    .pixelSize(0.3)              
    .tileSize(1024)
    //.cellExpansion(0.5)            
    .measureShape()              
    .measureIntensity()          
    .build()
	
//def annotations = getAnnotationObjects()
def annotations = QP.getSelectedObjects()

if (annotations.isEmpty()) {
    QP.getLogger().error("No parent objects are selected!")
    return
}

// Run detection for the selected objects
stardist.detectObjects(imageData, annotations)

stardist.close()
println 'Done!'

