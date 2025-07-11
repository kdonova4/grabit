import React from "react";
import ProductViewer from "./ProductViewer";
import ProductByCategory from "./TestCalls/ProductTestCalls/ProductsByCategory";

const Home: React.FC = () => {


    return(
        <>
        <ProductViewer/>
        <ProductByCategory/>
        </>
    )
}

export default Home;