import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Firebase (liest GoogleService-Info.plist) + Koin starten
        MainViewControllerKt.doInitIosApp()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
