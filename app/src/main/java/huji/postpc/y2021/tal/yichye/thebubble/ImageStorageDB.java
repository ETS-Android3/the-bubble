package huji.postpc.y2021.tal.yichye.thebubble;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageStorageDB {

    private FirebaseStorage storageDB;
    private static ImageStorageDB instance = null;

    private ImageStorageDB() {
        storageDB = FirebaseStorage.getInstance();
        instance = this;
    }

    public static ImageStorageDB getInstance()
    {
        if (instance == null)
        {
            return new ImageStorageDB();
        }
        return instance;
    }


    public StorageReference createReference(String userName, String imageName)
    {
        return storageDB.getReference().child(userName).child(imageName);
    }

}