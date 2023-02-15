package com.practise.secureauthentication.presentation.navigation

//
//@Composable
//fun NavigationGraph() {
//
//    val navController = rememberNavController()
//
//    NavHost(navController, Screen.LOGIN.route) {
//
//        composable(Screen.LOGIN.route) {
//            LoginScreen {
//                navController.navigate(Screen.PROFILE.route) {
//                    popUpTo(route= Screen.LOGIN.route) {
//                        inclusive = true
//                    }
//                }
//            }
//        }
//
//        composable(Screen.PROFILE.route) {
//            ProfileScreen {
//                navController.navigate(Screen.LOGIN.route) {
//                    popUpTo(route = Screen.PROFILE.route) {
//                        inclusive = true
//                    }
//                }
//            }
//        }
//    }
//}