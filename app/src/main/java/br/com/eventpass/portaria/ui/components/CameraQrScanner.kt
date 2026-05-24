package br.com.eventpass.portaria.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import br.com.eventpass.portaria.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors

@Composable
fun CameraQrScanner(
    modifier: Modifier = Modifier,
    onQrDetectado: (String) -> Unit
) {
    val contexto       = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor       = remember { Executors.newSingleThreadExecutor() }

    var temPermissao by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val solicitarPermissao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida -> temPermissao = concedida }

    LaunchedEffect(Unit) {
        if (!temPermissao) solicitarPermissao.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    if (!temPermissao) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📷", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.msg_permissao_necessaria), color = Color.White)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { solicitarPermissao.launch(Manifest.permission.CAMERA) }) {
                    Text(stringResource(R.string.camera_permission_button))
                }
            }
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ── Câmera ─────────────────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                var ultimoQr = ""

                ProcessCameraProvider.getInstance(ctx).also { futuro ->
                    futuro.addListener({
                        val provider = futuro.get()
                        val preview  = Preview.Builder().build()
                            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                        val analise = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build().also { ia ->
                                ia.setAnalyzer(executor) { proxy ->
                                    val conteudo = decodificarQr(proxy)
                                    if (conteudo != null && conteudo != ultimoQr) {
                                        ultimoQr = conteudo
                                        onQrDetectado(conteudo)
                                        Thread.sleep(3000)
                                        ultimoQr = ""
                                    }
                                    proxy.close()
                                }
                            }

                        try {
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview, analise
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // ── Overlay ────────────────────────────────────────────────────────────
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(3.dp, Color.White, RoundedCornerShape(16.dp))
            )
            Text(
                stringResource(R.string.msg_aponte_qr_convidado),
                fontSize  = 14.sp,
                color     = Color.White,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 56.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

private fun decodificarQr(imageProxy: ImageProxy): String? {
    val buffer = imageProxy.planes[0].buffer
    val bytes  = ByteArray(buffer.remaining()).also { buffer.get(it) }
    return try {
        val source = PlanarYUVLuminanceSource(
            bytes, imageProxy.width, imageProxy.height,
            0, 0, imageProxy.width, imageProxy.height, false
        )
        MultiFormatReader().apply {
            setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
        }.decode(BinaryBitmap(HybridBinarizer(source))).text
    } catch (_: NotFoundException) { null }
     catch (_: Exception)          { null }
}
