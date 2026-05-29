- **train_rgb_custom**  
  Pipeline to train a 3-channel StarDist model using custom training data.

- **train_fluo_custom**  
  Pipeline to train a 1-channel StarDist model using custom training data.

- **retrain_rgb_model**  
  Pipeline to retrain StarDist’s pre-existing 3-channel model using custom training data.

- **retrain_fluo_model**  
  Pipeline to retrain StarDist’s pre-existing 1-channel model using custom training data.

#### QuPath Scripts

- **create_training_labels**  
  Groovy script used to create training images and corresponding masks for model training.

- **delete_annotation**  
  Script to delete annotations.  
  Workflow: first annotate a region, run detection model, and if any detections are incorrect, select them and run this script to remove them.

- **starDist_he**
script to use starDist 3 channel model

- **starDist**
script to use starDist 1 channel model