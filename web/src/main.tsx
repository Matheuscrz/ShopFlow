import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "./index.css";

const savedTheme = localStorage.getItem("theme-preference");
const isSystemDark = window.matchMedia("(prefers-color-scheme: dark)").matches;

if (savedTheme === "dark" || (!savedTheme && isSystemDark)) {
  document.documentElement.classList.add("dark");
} else {
  document.documentElement.classList.remove("dark");
}

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
