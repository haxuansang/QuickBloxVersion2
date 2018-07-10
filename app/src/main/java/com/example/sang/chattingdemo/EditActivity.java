package com.example.sang.chattingdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {
    CircleImageView imvEditImageUser;
    ProgressDialog editProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        imvEditImageUser=(CircleImageView)findViewById(R.id.edit_image_user);
        editProgress = new ProgressDialog(this);
        editProgress.setMessage("Loading Profile...");
        editProgress.show();
        loadUserImage();
        imvEditImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(pickIntent,"Select Picture"),7171);

            }
        });

    }

    private void loadUserImage() {
        QBUsers.getUser(QBChatService.getInstance().getUser().getId()).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                if(qbUser.getFileId()!=null)
                {
                    QBContent.getFile(qbUser.getFileId()).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            String URL=qbFile.getPublicUrl();
                            Picasso.with(getBaseContext()).load(URL).into(imvEditImageUser, new Callback() {
                                @Override
                                public void onSuccess() {
                                    editProgress.dismiss();

                                }

                                @Override
                                public void onError() {

                                }
                            });

                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Can't retrieve image user", Toast.LENGTH_SHORT).show();
                    editProgress.dismiss();
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==7171)
            {
                Uri selectedImage = data.getData();
                final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
                progressDialog.setMessage("Processing");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                try {
                    InputStream in = getContentResolver().openInputStream(selectedImage);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                    File file = new File(Environment.getExternalStorageDirectory()+"/image.png");
                    if (file.exists())
                           file.delete();
                    FileOutputStream fileOutputStream =new FileOutputStream(file);
                    fileOutputStream.write(bos.toByteArray());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    int imageSizeKb=(int)file.length()/1024;
                    if(imageSizeKb>=(1024*100))
                    {
                        Toast.makeText(this, "Error Size", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    QBContent.uploadFileTask(file,true,null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            int userFileID=qbFile.getId();
                            QBUser qbUser = new QBUser();
                            qbUser.setId(QBChatService.getInstance().getUser().getId());
                            qbUser.setFileId(userFileID);
                            QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                    imvEditImageUser.setImageBitmap(bitmap);
                                    progressDialog.dismiss();
                                    Toast.makeText(EditActivity.this, "Update Image Successfully", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(EditActivity.this, "Upload Image Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }
}
