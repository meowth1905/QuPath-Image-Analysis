
def channel_of_interest =  null  // null to export all the channels 
def downsample = 1


println("Image: "+getProjectEntry().getImageName())
def training_regions = getAnnotationObjects().findAll { it.getPathClass() == getPathClass("Training") }

//def validation_regions = getAnnotationObjects().findAll { it.getPathClass() == getPathClass("Validation") }

if (training_regions.size() > 0 ) saveRegions( training_regions, channel_of_interest, downsample, 'train')

//if (validation_regions.size() > 0 ) saveRegions( validation_regions, channel_of_interest, downsample, 'test')

println "done"

def saveRegions( def regions, def channel, def downsample, def type ) {
	// Randomize names
  	def is_randomized = getProject().getMaskImageNames()
    getProject().setMaskImageNames(true)
    
    def rm = RoiManager.getRoiManager() ?: new RoiManager()
    // Get the image name
    def image_name = getProjectEntry().getImageName()
    regions.eachWithIndex{ region, region_idx ->
        println("Processing Region #"+(  region_idx + 1 ) )
        
        def file_name = "${image_name}_${type}_region${region_idx + 1}"
        imageData = getCurrentImageData();
        server = imageData.getServer();
        viewer = getCurrentViewer();
        hierarchy = getCurrentHierarchy();

        pathImage = null;
	request = RegionRequest.createInstance(imageData.getServerPath(), downsample, region.getROI())
        pathImage = IJExtension.extractROIWithOverlay(server, region, hierarchy, request, false, viewer.getOverlayOptions());
        image = pathImage.getImage()
        //println("Image received" )
        //image.show()
        // Create the Labels image
        def labels = IJ.createImage( "Labels", "16-bit black", image.getWidth(), image.getHeight() ,1 );
        rm.reset()
        
        IJ.run(image, "To ROI Manager", "")
        
        def rois = rm.getRoisAsArray() as List
        //println("Creating Labels" )
        
        def label_ip = labels.getProcessor()
        def idx = 0
        print( "Ignoring Rectangles: " )
        rois.each{ roi ->
            if (roi.getType() == Roi.RECTANGLE) {
                print( "." )
            } else {
                label_ip.setColor( ++idx )
                label_ip.setRoi( roi )
                label_ip.fill( roi )
            }
        }
        print("\n")
        labels.setProcessor( label_ip )
        
        //labels.show()
        
        // Split to keep only channel of interest
        def output = image
        if  ( channel != null ){
            imp_chs =  ChannelSplitter.split( image )
            output = imp_chs[  channel - 1 ]
        }
        
        saveImages(output, labels, file_name, type)
                
        //println( file_name + " Image and Mask Saved." )
        
        // Save some RAM
        output.close()
        labels.close()
        image.close()
    }

    // Return Project setup as it was before
    getProject().setMaskImageNames( is_randomized )
}

// This will save the images in the selected folder
def saveImages(def images, def labels, def name, def type) {
    def source_folder = new File ( buildFilePath( PROJECT_BASE_DIR, 'ground_truth', type, 'images' ) )
    def target_folder = new File ( buildFilePath( PROJECT_BASE_DIR, 'ground_truth', type, 'masks' ) )
    mkdirs( source_folder.getAbsolutePath() )
    mkdirs( target_folder.getAbsolutePath() )
    
    IJ.save( images , new File ( source_folder, name ).getAbsolutePath()+'.tif' )
    IJ.save( labels , new File ( target_folder, name ).getAbsolutePath()+'.tif' )
}

// Manage Imports
import qupath.lib.roi.RectangleROI
import qupath.imagej.gui.IJExtension;
import ij.IJ
import ij.gui.Roi
import ij.plugin.ChannelSplitter
import ij.plugin.frame.RoiManager