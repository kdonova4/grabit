import { useEffect, useState } from "react";
import { AppUserVerify } from "../../types/AppUser/AppUserVerify";
import { verify } from "../../api/AppUserAPI";

const Verify: React.FC = () => {

    const [credentials, setCredentials] = useState<AppUserVerify>({
        email: "",
        code: ""
    })
    
    const [errors, setErrors] = useState<string | null>(null);
    const [message, setMessage] = useState("");

    useEffect(() => {
        const savedEmail = localStorage.getItem("email");
        if(savedEmail) {
            setCredentials((prev) => ({...prev, email: savedEmail}));
        }
    }, [])

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {

        e.preventDefault();

        setErrors(null);

        try{
            const data = await verify(credentials);
            console.log(data);
            localStorage.removeItem("email");
            setMessage("Verification Succesful")
        }catch(e) {
            setErrors((e as Error).message)
            setMessage("Verification Failed")
        }
    }

    return (<>
        <div>
                <h2 className="text-xl font-bold mb-2">Verify Email</h2>

                <div className="p-4">
                    <form onSubmit={handleSubmit}>
                        <h2 className="text-xl font-bold mb-2">Enter Verification Code</h2>
                        <input
                            type="text"
                            value={credentials.code}
                            onChange={(e) => setCredentials((prev) => ({ ...prev, code: e.target.value }))}
                            className="border px-2 py-1 mr-2"
                            placeholder="Enter Password"
                            required
                        />
                        <button type="submit" className="bg-blue-600 text-white px-3 py-1 rounded">
                            Verify Email
                        </button>

                    </form>


                    {message && <p className="text-red-600 mt-2">{message}</p>}
                    {errors && <p className="text-red-600 mt-2">{errors}</p>}
                </div>
            </div>
    </>)
}

export default Verify;