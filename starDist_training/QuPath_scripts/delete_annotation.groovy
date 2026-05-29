import qupath.lib.objects.PathObject

def imageData = getCurrentImageData()
if (imageData == null) {
    print "No image open."
    return
}

def selected = getSelectedObjects()

if (selected == null || selected.isEmpty()) {
    print "No objects selected."
    return
}

// Delete all selected objects at once
removeObjects(selected, true)

print "Deleted ${selected.size()} selected object(s)."