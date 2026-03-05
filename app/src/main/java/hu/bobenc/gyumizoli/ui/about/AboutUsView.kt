package hu.bobenc.gyumizoli.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsView(
    onBackClick: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val darkGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF4CAF50)
    val bgColor = Color(0xFFF9F9F9)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rólunk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Vissza", tint = darkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = darkGreen
                )
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Üdvözöljük a\nGyümiZöli-nél!",
                color = darkGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Célunk, hogy a legfrissebb és legfinomabb gyümölcsöket és zöldségeket kínáljuk Önnek közvetlenül tőlünk. Hiszünk abban, hogy a friss, természetes alapanyagok nemcsak az egészségünkre, hanem az életminőségünkre is pozitív hatással vannak.",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            SectionTitle(title = "Miért válasszon minket?", darkGreen = darkGreen, lightGreen = lightGreen)

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                AboutCard(
                    icon = Icons.Rounded.Inventory,
                    title = "Frissesség",
                    text = "Minden termékünket gondosan válogatjuk, hogy biztosítsuk a legmagasabb minőséget. A gyümölcsök és zöldségek közvetlenül a termelőtől érkeznek, így garantáljuk a legfrissebb ízeket.",
                    iconColor = lightGreen,
                    titleColor = darkGreen
                )

                AboutCard(
                    icon = Icons.Rounded.Eco,
                    title = "Fenntarthatóság",
                    text = "Elkötelezettek vagyunk a fenntartható mezőgazdaság mellett. Támogatjuk a helyi termelőket, és törekszünk arra, hogy csökkentsük a környezeti lábnyomunkat.",
                    iconColor = lightGreen,
                    titleColor = darkGreen
                )

                AboutCard(
                    icon = Icons.Rounded.LocalShipping,
                    title = "Kényelem",
                    text = "Webshopunk lehetővé teszi, hogy otthonról, kényelmesen rendeljen. Gyors és megbízható szállítást kínálunk, hogy Önnek csak a választásra kelljen koncentrálnia.",
                    iconColor = lightGreen,
                    titleColor = darkGreen
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            SectionTitle(title = "Elérhetőségek", darkGreen = darkGreen, lightGreen = lightGreen)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ContactRow(icon = Icons.Rounded.LocationOn, title = "Cím", text = "1065 Budapest Révay utca 16.", iconColor = lightGreen)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    ContactRow(
                        icon = Icons.Rounded.AccessTime,
                        title = "Nyitvatartás",
                        text = "Hétfő - Péntek: 9:00 - 17:00\nSzombat: 10:00 - 15:00\nVasárnap: Zárva",
                        iconColor = lightGreen
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    ContactRow(icon = Icons.Rounded.Email, title = "E-mail", text = "gyumizoli10@gmail.com", iconColor = lightGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    ContactRow(icon = Icons.Rounded.Phone, title = "Telefon", text = "+36 (20) 123-4567", iconColor = lightGreen)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            SectionTitle(title = "Rólunk mondták", darkGreen = darkGreen, lightGreen = lightGreen)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = lightGreen.copy(alpha = 0.05f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    TestimonialItem(
                        quote = "A legfrissebb termékeket vásároltam online!",
                        author = "Bence B.",
                        iconColor = lightGreen
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))

                    TestimonialItem(
                        quote = "Gyors szállítás és nagyszerű ügyfélszolgálat!",
                        author = "Gergő M.",
                        iconColor = lightGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            SectionTitle(title = "Küldetésünk", darkGreen = darkGreen, lightGreen = lightGreen)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Küldetésünk, hogy egészséges és ízletes gyümölcsöket és zöldségeket kínáljunk, amelyek hozzájárulnak az Ön és családja jólétéhez.",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Fedezze fel kínálatunkat, és tapasztalja meg a frissesség ízét!\nKöszönjük, hogy minket választott!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = darkGreen
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onNavigateHome,
                        colors = ButtonDefaults.buttonColors(containerColor = lightGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Böngésszen termékeink között", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


@Composable
fun SectionTitle(title: String, darkGreen: Color, lightGreen: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            color = darkGreen,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .width(64.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(lightGreen)
        )
    }
}

@Composable
fun AboutCard(
    icon: ImageVector,
    title: String,
    text: String,
    iconColor: Color,
    titleColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = titleColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = text,
                color = Color.DarkGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, title: String, text: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.padding(top = 2.dp).size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, fontSize = 15.sp, color = Color.Gray, lineHeight = 22.sp)
        }
    }
}

@Composable
fun TestimonialItem(quote: String, author: String, iconColor: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Rounded.FormatQuote,
            contentDescription = "Idézet",
            tint = iconColor.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "\"$quote\"",
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "- $author",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
        }
    }
}