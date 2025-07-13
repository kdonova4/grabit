import { useState } from "react";
import { useAuth } from "../../AuthContext";
import { authenticate } from "../../api/AppUserAPI";
import { AppUserAuthRequest } from "../../types/AppUser/AppUserAuthRequest";
import { useNavigate } from "react-router-dom";

const Login: React.FC = () => {

    const [errors, setErrors] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const [credentials, setCredentials] = useState<AppUserAuthRequest>({
        username: "",
        password: ""
    })
    const [message, setMessage] = useState("");
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        setLoading(true);
        setErrors(null);

        

        try {
            const data = await authenticate(credentials)
            console.log(data);
            login(data.jwt_token);
            setMessage("Login Succesful")
            navigate('/');
        } catch(e) {
            setErrors((e as Error).message);
            setMessage("Login Failed")
        } finally {
            setLoading(false);
        }
    }

    return (
        
        <>

            <div className="p-4">
                <form onSubmit={handleSubmit}>
                <h2 className="text-xl font-bold mb-2">Enter Username</h2>
                <input
                    type="text"
                    value={credentials.username}
                    onChange={(e) => setCredentials((prev) => ({...prev, username: e.target.value}))}
                    className="border px-2 py-1 mr-2"
                    placeholder="Enter Username"
                    required
                />
                <h2 className="text-xl font-bold mb-2">Enter Password</h2>
                <input
                    type="password"
                    value={credentials.password}
                    onChange={(e) => setCredentials((prev) => ({...prev, password: e.target.value}))}
                    className="border px-2 py-1 mr-2"
                    placeholder="Enter Password"
                    required
                />
                <button type="submit" disabled={loading} className="bg-blue-600 text-white px-3 py-1 rounded">
                    {loading ? "Logging In..." : "Login"}
                </button>

                </form>
                

                {message && <p className="text-red-600 mt-2">{message}</p>}
                {errors && <p className="text-red-600 mt-2">{errors}</p>}
            </div>
        </>

    )
}

export default Login;