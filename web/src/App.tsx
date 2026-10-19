import { OfflineBanner } from "./components/ui/OfflineBanner";
import { AppRoutes } from "./routes/AppRoutes";

export default function App() {
  return (
    <>
      <OfflineBanner />
      <AppRoutes />
    </>
  );
}
