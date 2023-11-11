import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    // @State private var orientation = UIDevice.current.orientation
    
    var body: some View {
        VStack {
            ComposeView()
                    .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }.detectOrientation(
            //$orientation
        ).onAppear() {
            Main_iosKt.changeScreenOrientation { KotlinInt in
                if (KotlinInt == 0) {
                    changeOrientation(to: UIInterfaceOrientation.portrait)
                }
                else {
                    changeOrientation(to: UIInterfaceOrientation.landscapeLeft)
                }
            }
            
            Main_iosKt.setVibrateCallback { KotlinInt in
                vibrate(whitch: Int(truncating: KotlinInt) )
            }
        }
    }
}

struct DetectOrientation: ViewModifier {
    
    // @Binding var orientation: UIDeviceOrientation
    
    func body(content: Content) -> some View {
        content
            .onReceive(NotificationCenter.default.publisher(for: UIDevice.orientationDidChangeNotification)) { _ in
                // 触发屏幕方向改变事件
                if (UIDevice.current.orientation.isLandscape) {
                    Main_iosKt.onScreenChange(orientation: 1)
                }
                else {
                    Main_iosKt.onScreenChange(orientation: 0)
                }
                // orientation = UIDevice.current.orientation
            }
    }
}

extension View {
    func detectOrientation(
        //_ orientation: Binding<UIDeviceOrientation>
    ) -> some View {
        modifier(DetectOrientation(
            //orientation: orientation
        ))
    }
}

func changeOrientation(to orientation: UIInterfaceOrientation) {
    if #available(iOS 16.0, *) {

        let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene

        if (orientation.isPortrait) {
            windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: .portrait))
        }
        else {
            windowScene?.requestGeometryUpdate(.iOS(interfaceOrientations: .landscape))
        }
    }
    else {
        UIDevice.current.setValue(orientation.rawValue, forKey: "orientation")
    }
}

func vibrate(whitch type: Int) {
    switch type {
    case 0:
        UIImpactFeedbackGenerator(style: .light).impactOccurred()
        break
    case 1:
        UINotificationFeedbackGenerator().notificationOccurred(.success)
        break
    case 2:
        UINotificationFeedbackGenerator().notificationOccurred(.error)
        break
    case 3:
        UINotificationFeedbackGenerator().notificationOccurred(.warning)
        break
    default:
        break
    }
}
