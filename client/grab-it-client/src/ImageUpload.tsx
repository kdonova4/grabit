import { useEffect, useState } from "react";
import { ImageResponse } from "./types/Image/ImageResponse";
import { useParams } from "react-router-dom";
import { useAuth } from "./AuthContext";
import { deleteImageById, fetchImagesByProduct, uploadImage } from "./api/ImageAPI";

type ImageUploadProps = {
    files: File[];
    images: ImageResponse[];
    setImages: React.Dispatch<React.SetStateAction<ImageResponse[]>>
    setFiles: React.Dispatch<React.SetStateAction<File[]>>;
    fetchImages: () => Promise<void>;
    pendingDeletes: number[];
    setPendingDeletes: React.Dispatch<React.SetStateAction<number[]>>

}

const ImageUpload: React.FC<ImageUploadProps> = ({ pendingDeletes, setPendingDeletes, fetchImages, files, images, setImages, setFiles }) => {

    const [errors, setErrors] = useState<string[]>([]);
    const { id } = useParams();
    const { token, appUserId } = useAuth();

    
    
    

    const handleUpload = async (files: File[]) => {
        if (!token) {
            setErrors(["User Not Authenticated"]);
            return;
        }

        try {
            const data = await uploadImage(Number(id), files, token)

        } catch (e) {
            setErrors([(e as Error).message])
        }
    }

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const selectedFiles = event.target.files;
        if(selectedFiles && selectedFiles.length > 0) {
            const newFiles = Array.from(selectedFiles);

            setFiles(prevFiles => [...prevFiles, ...newFiles]);
        }
    }

    const handleRemoveImage = (fileToRemove: File) => {
        setFiles(prevFiles =>
            prevFiles.filter(file => file !== fileToRemove)
        )
    }

    const handleDeleteImage = async (ImageToDelete: number) => {
        if(!token) {
            throw new Error("User Not Authorized")
        }

        setImages(prev => prev.filter(image => image.imageId !== ImageToDelete));
        setPendingDeletes(prev => [...prev, ImageToDelete]);
    }

    return (
        <>
            {images.map(image => (
                <img key={image.imageId} src={image.imageUrl} alt="preview" className="tiny-preview" onClick={() => handleDeleteImage(image.imageId)}/>
            ))}
            <h2>Add Images</h2>
            <fieldset className="form-group">
                <label htmlFor="images">images</label>
                <input
                    id="images"
                    name="images"
                    type="file"
                    className="formControl"
                    accept="image/*"
                    multiple
                    onChange={handleImageChange}

                />
            </fieldset>
            {files.map(file => (
                <img key={file.name} src={URL.createObjectURL(file)} alt="preview" className="tiny-preview" onClick={() => handleRemoveImage(file)}/>
            ))}
        </>
    )
}

export default ImageUpload;