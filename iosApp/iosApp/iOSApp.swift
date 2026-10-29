import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Firebase und Koin starten
        MainViewControllerKt.doInitIosApp()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
