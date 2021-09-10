package huji.postpc.y2021.tal.yichye.thebubble;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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

    public void deleteImage(String userName, String imageName)
    {
        this.createReference(userName, imageName).delete();
    }

    public MutableLiveData<Boolean> uploadImage(String userName, String imageName, Bitmap image)
    {
        MutableLiveData<Boolean> uploadLiveData = new MutableLiveData<Boolean>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = this.createReference(userName, imageName);
        UploadTask uploadTask = ref.putBytes(data);

        uploadTask.addOnFailureListener(exception -> uploadLiveData.setValue(false))
                .addOnSuccessListener(taskSnapshot -> uploadLiveData.setValue(true));

        return uploadLiveData;
    }

}